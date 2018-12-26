public static int getItself(int itself, int dummy)
{
    return itself;
}

public static void main(String[] args)
{
    int a = 10;
    int b = 20;

    a = getItself(b, b = a);
}