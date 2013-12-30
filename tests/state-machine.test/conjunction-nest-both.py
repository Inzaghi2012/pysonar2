x = int()

if 2 < x and x < 5:
    if x < 6:
        y = x     # [3..4]
    else:
        y = x

    if x > 6:
        z = x
    else:
        z = x     # [3..4]

else:
    y = x         # [..2] | [5..]
    z = x         # [..2] | [5..]

print y, z
