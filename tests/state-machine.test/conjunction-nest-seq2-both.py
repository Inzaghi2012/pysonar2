x = int()

if 2 < x < 5:
    if x < 6:
        y = 42
    else:
        y = 'hi'

    if x < 4:
        y = 42          # here
    else:
        y = 'hi'        # and here

print y
