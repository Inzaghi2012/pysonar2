x = int()

if 0 < x and x < 10:
    if x < 6:
        y = x
    else:
        y = x

    if x < 5:
        y = x        # [1..4]
    else:
        y = x        # [5..9]

print y
