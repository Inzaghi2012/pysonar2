x = int()

if not (x > 5):
    if x < 2:
        y = x     # (-inf, 2)
    else:
        y = x     # [2, 5]
else:
    y = x         # (5, +inf)

print y
