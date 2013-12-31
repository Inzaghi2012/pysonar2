x = int()


if not (0 < x and x < 10):
    if x < 5:
        y = x     # [..0]
    else:
        y = x     # [10..]
else:
    y = x         # [1..9]

print y
