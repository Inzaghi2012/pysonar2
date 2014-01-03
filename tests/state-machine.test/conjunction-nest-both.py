x = int()

if 2 < x and x < 5:
    if x < 6:
        y = x     # (2, 5)
    else:
        y = x

    if x > 6:
        z = x
    else:
        z = x     # (2, 5)

else:
    y = x         # (-inf, 2] [5, +inf)
    z = x         # (-inf, 2] [5, +inf)

print y, z
