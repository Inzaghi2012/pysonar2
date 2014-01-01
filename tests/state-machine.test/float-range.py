x = float(0)

if 1.5 < x and x < 10.3:
    if x < 6.0:
        y = x      # [1.5 .. 6.0]
    else:
        y = x      # [6.0 .. 10.3]
else:
    y = x          # [..1.5] | [10.3..]

print y


# inner condition must be true
if x < 0.12 or x > 15.34:
    if x < 5.3 or x > 10.9:
        u = x      # [..0.12] [15.34..]
    else:
        u = x

print u


# inner condition must be false
if x < 0.12 or x > 15.34:
    if x > 5.3 and x < 10.9:
        v = x
    else:
        v = x      # [..0.12] [15.34..]

print v
