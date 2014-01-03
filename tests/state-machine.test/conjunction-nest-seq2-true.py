x = int()

if 0 < x and x < 10:
    if x < 6:
        y = x
    else:
        y = x

    if x < 15:
        y = x          # (0, 6) [6, 10)  simplify: (0, 10)
    else:
        y = x        

print y
