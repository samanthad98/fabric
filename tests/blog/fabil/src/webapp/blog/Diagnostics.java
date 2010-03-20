package webapp.blog;

import java.net.InetAddress;

import fabric.util.Map;

import jif.lang.Label;

public class Diagnostics {

  public static final int NUM_INITIAL_BLOGS = 50;
  public static final int NUM_INITLAL_COMMENTS = 5;

  private static LocalCore localCore;
  private static Core core;
  private static Label currentLabel;
  
  public static LocalCore getLocalCore() {
    return localCore;
  }
  
  public static Core getCore() {
    return core;
  }
  
  public static Label getCurrentLabel() {
    return currentLabel;
  }
  
  public static void initializeFabric() {
    localCore = Worker.getWorker().getLocalCore();
    core = Worker.getWorker().getCore(System.getProperty("blog.core", "core0"));
    currentLabel = localCore.getEmptyLabel();
  }
  
  public static void createDatabase() throws TransactionFailure {
    long start = 0;
    atomic {
      Map root = (Map) core.getRoot();
      Blog instance = (Blog)root.get("blog");
      if(instance == null) {
        atomic {
          Blog.createNewInstance(core, currentLabel);
          //TODO: bug?
          //root.put("blog", Blog.getInstance());
          Blog.getInstance().emptyDatabase();
          addBlogsAndComments(NUM_INITIAL_BLOGS, NUM_INITLAL_COMMENTS);
        }
      } else {
        Blog.setInstance(instance);
      }
      start = System.currentTimeMillis();
    }
    Statistics.getInstance().addTransaction(System.currentTimeMillis() - start);
    
  }

  private static String duplicateString(String msg, int n) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < n; i++) {
      b.append(msg);
    }
    return b.toString();
  }

  public static void addBlogs(int n) throws TransactionFailure {
    for (int i = 0; i < n; i++) {
      Transactions.addBlogPost("Title", duplicateString(
          "This is a blog post. ", 50), core, currentLabel);
    }
  }

  public static void addComments(BlogPost p, int m) throws TransactionFailure {
    for (int i = 0; i < m; i++) {
      Transactions.addComment("username", duplicateString(
          "This is a comment. ", 10), p, core, currentLabel);
    }
  }

  public static void addBlogsAndComments(int n, int m)
      throws TransactionFailure {
    for (int i = 0; i < n; i++) {
      long start = 0;
      atomic {
        BlogPost p = Transactions.addBlogPost("Title", duplicateString(
            "This is a blog post. ", 50), core, currentLabel);
        for (int j = 0; j < m; j++) {
          Transactions.addComment("username", duplicateString(
              "This is a comment. ", 10), p, core, currentLabel);
        }

        start = System.currentTimeMillis();
      }
      Statistics.getInstance().addTransaction(System.currentTimeMillis() - start);
    }
  }

}
