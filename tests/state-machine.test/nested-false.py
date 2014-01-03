x = int()

if x < 2:
    if x > 5:
        y = x
    else:
        y = x     # (-inf, 2)
else:
    y = x         # [2, +inf)

print y
