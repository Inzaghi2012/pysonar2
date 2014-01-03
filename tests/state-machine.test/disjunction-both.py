x = int()
y = int()

if x < 0 or x > 10:
    if x < 5:
        w = x      # (-inf, 0)
    else:
        w = x      # (10, +inf)

print w            # (-inf, 0) (10, +inf)
