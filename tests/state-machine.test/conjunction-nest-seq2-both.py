x = int()

if 2 < x and x < 5:
    if x < 6:
        y = x
    else:
        y = x

    if x < 4:
        y = x        # [2..4]
    else:
        y = x        # [4..5]

print y
