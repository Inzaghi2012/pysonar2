x = int()

if 2 < x < 5:
    if x < 6:
        y = x      # [2..5]
    else:
        y = x
else:
    y = x          # [..2] | [5..]

print y
