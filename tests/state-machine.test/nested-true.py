x = int()

if x < 2:
    if x < 5:
        y = x     # [..1]
    else:
        y = x
else:
    y = x         # [2..]

print y
