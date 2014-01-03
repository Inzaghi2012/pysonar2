# mixed float and int bounds
x = int()

if 1.5 < x and x < 10:
    if x < 6.2:
        w = x      # [2, 6]
    else:
        w = x      # [7, 10)
else:
    w = x          # (-inf, 1] [10, +inf)

print w
