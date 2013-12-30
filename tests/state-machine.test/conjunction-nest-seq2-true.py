x = int()

if 0 < x and x < 10:
    if x < 6:
        y = x
    else:
        y = x

    if x < 15:
        y = x          # [1..9]
    else:
        y = x        

print y
