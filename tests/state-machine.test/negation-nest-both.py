x = int()

if not (x > 5):
    if x < 2:
        y = x     # [..1]
    else:
        y = x     # [2..5]
else:
    y = x         # [6..]

print y
