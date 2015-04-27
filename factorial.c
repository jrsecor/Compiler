/*This is a C- File*/
int i;
int hello;
int array[42];

int fact( int x )
/* recursive factorial function */
{ if (x > 1)
	return x * fact(x-1);
  else
	return 1;
}



void main( void )
{ int x;
  array[1+3] = array[x];
  x = read();
  if(x > 0) write( fact(x) );
  x = (7 + 3) * 82 - fact(x);
}