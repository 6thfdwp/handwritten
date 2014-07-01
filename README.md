handwritten
===========

UQ COMP7702 AI-A3

`push`        


`~/.gitconfig`

[The Magic of XOR](http://www.cs.umd.edu/class/sum2003/cmsc311/Notes/BitOp/xor.html)



[XOR Properties](http://devenbhooshan.wordpress.com/2012/07/08/xor-properties/)



[Bitwise Operators](http://www.cprogramming.com/tutorial/bitwise_operators.html)



```python
x = x ^ y
y = x ^ y     # y<-x (x ^ y) ^ y = x ^ (y ^ y) = x ^ 0
x = x ^ y     # (x ^ y) ^ x = (x ^ x) ^ y = 0 ^ y
```

```python
"""
 Need to address when bits in x and y are both set
 By doing 'complement', then 'and', we got two '0' to be applied OR
 This holds true when both bits are not set or either one is set
"""
x ^ y = (~x & y) | (x & ~y)
```

```python
import timeit

timeit.timeit(stmt="pass", setup="pass", timer=<default>, number=1000000)
# or
timeit.Timer(stmt="pass", setup="pass", timer=<default>).timeit(number=1000000)
```

```python
x ^ y = y ^ x                  # communtative
(x ^ y) ^ z = x ^ (y ^ z)      # associative
x ^ x = 0
x ^ 0 = x                      # 0^0=0 1^1=0
```
