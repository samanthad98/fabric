package regression;

public class Flatten01 {
  Flatten01 f;

  int m() {
    int i = 0 + 1 + 2;
    i = i + i;
    i = i + i + i;
    i = this.m();
    i = this.f.f.f.m();
    return i + i;
  }
}

