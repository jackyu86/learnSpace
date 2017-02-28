package Miscellaneous;
    public class Fig01_02
    {

/* START: Fig01_02.txt */
        public static int f( int x )
        {
/* 1*/      if( x == 0 ){
/* 2*/          return 0;
			} else{
            	int a = 2 * f( x - 1 ) + x * x;
            	System.out.println(a);
/* 3*/          return a;
            }
        }
/* END */

        public static void main( String [ ] args )
        {
            System.out.println( "f(5) = " + f( 5 ) );
        }
    }
