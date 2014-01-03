x = int()

if not (x > 5):
    if x < 10:
        y = x     # (-inf, 5]
    else:
        y = x
else:
    y = x         # (5, +inf)

print y
