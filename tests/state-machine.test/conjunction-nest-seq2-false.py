x = int()

if 2 < x < 5:
    if x < 6:
        y = 42
    else:
        y = 'hi'

    if x > 6:
        y = 42
    else:
        y = 'hi'        # refers only this one

print y
