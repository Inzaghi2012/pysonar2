x = int()

if 0 < x and x < 10:
    if x < 6:
        y = x
    else:
        y = x

    if x < 5:
        y = x        # (0, 5)
    else:
        y = x        # [5, 6) [6, 10)  --> [5, 10)

print y
