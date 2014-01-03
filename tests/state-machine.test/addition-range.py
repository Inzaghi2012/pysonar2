x = int()
y = int()


if x < 20:
    if y < x + 10:
        u = y   # (-inf, 30)
    else:
        u = y   # any
else:
    u = y       # any

print u
