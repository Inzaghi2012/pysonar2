x = int()


if not (0 < x and x < 10):
    if x < 5:
        y = x     # (-inf, 0]
    else:
        y = x     # [10, +inf)
else:
    y = x         # (0, 10)

print y
