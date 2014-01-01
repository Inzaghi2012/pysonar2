x = 1.2
y = 2.3
ep = 0.0000001


if x + y == 3.5:
    u = 42
else:
    u = 'hi'

print u


if 1.1 - ep < y - x < 1.1 + ep:
    u = 42
else:
    u = 'hi'

print u

if 2.76 - ep < x * y < 2.76 + ep:
    u = 42
else:
    u = 'hi'

print u


if 1.9166666 < y / x < 1.9166667:
    u = 42
else:
    u = 'hi'

print u

