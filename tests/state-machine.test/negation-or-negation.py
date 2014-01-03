x = int()

# not (x < 0 or (not (x <= 10))) == not (x < 0 or x > 10) == 0 <= x <= 10

if not (x < 0 or (not (x <= 10))):
        y = x     # [0, 5)
    else:
        y = x     # [5, 10]
else:
    y = x         # (-inf, 0) (10, +inf)

print y
