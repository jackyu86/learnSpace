package Miscellaneous;
    public class Fig10_45
    {
/* START: Fig10_45.txt */
        public static double eval( int n )
        {
            double [ ] c = new double [ n + 1 ];

            c[ 0 ] = 1.0;
            for( int i = 1; i <= n; i++ )
            {
                double sum = 0.0;
                for( int j = 0; j < i; j++ )
                    sum += c[ j ];
                c[ i ] =  2.0 * sum / i + i;
            }

            return c[ n ];
        }
/* END */

        public static void main( String [ ] args )
        {
            System.out.println( "eval( 10 ) = " + eval( 10 ) );
        }
    }
