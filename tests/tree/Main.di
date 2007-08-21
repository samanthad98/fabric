package tree;

import java.util.Random;
import diaspora.client.AbortException;
import diaspora.client.Core;
import diaspora.client.Client;

public class Main {
  public static void main(String[] args) throws AbortException {
    Tree tree = null;
    atomic {
      Client client = diaspora.client.Client.getClient();
      Core treeCore = client.getCore(0L);
      Core nodeCore = client.getCore(1L);
      tree = new Tree@treeCore(nodeCore);
    }

    Random random = new Random();

    int cycle = 0;
    while (true) {
      for (int i = 0; i < 50; i++) {
	atomic {
	  int toInsert = random.nextInt();
	  tree.insertIterative(toInsert);
	}
      }

      for (int i = 0; i < 50; i++) {
	atomic {
	  int toFind = random.nextInt();
	  tree.lookup(toFind);
	}
      }

      System.out.println("cycle " + (cycle++));
    }
  }
}

/*
** vim: ts=2 sw=2 et cindent cino=\:0 syntax=java
*/
