x = int()

if x < 2:
    if x < 5:
        y = x     # (-inf, 2)
    else:
        y = x
else:
    y = x         # [2, +inf)

print y
