x = int()

if x < 3 or x > 10:
    if x < 2:
        w = x     # [..2]
    else:
        w = x     # [2..3] | [10..]

print w           # [..2] | [2..3] | [10..]
