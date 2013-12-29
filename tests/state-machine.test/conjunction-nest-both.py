x = int()

if 2 < x < 5:
    if x < 6:
        y = 42     # here
    else:
        y = 'hi'

    if x > 6:
        z = 42
    else:
        z = 'hi'    # here

print y,z
