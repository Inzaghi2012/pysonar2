x = int()

if x < 0 or x > 15:
    if x > 5 and x < 10:
        u = x
    else:
        u = x      # (-inf, 0) (15, +inf)

print u
