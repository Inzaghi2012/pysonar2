x = int()

if x <= 1:
    if x >= 1:
        if x == 1:
            y = x      # 1
        else:
            y = x
    else:
        y = x          # [..0]
else:
    y = x              # [2..]

print y
