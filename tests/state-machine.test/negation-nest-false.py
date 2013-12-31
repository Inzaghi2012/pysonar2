x = int()

if not (x > 5):
    if x > 10:
        y = x
    else:
        y = x     # [..5]
else:
    y = x         # [6..]

print y
