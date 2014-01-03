x = int()
y = int()


if 10 <= x <= 20:
    if x - 5 <= y <= x + 10:
        u = y   # [5, 30]
    else:
        u = y   # (-inf, 15) (20, +inf)
else:
    u = y       # any

print u
