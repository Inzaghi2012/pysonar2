# doule negation

x = int()

if not (not (x < 5)):
    if x < 10:
        y = x     # [..4]
    else:
        y = x
else:
    y = x         # [5..]

print y
