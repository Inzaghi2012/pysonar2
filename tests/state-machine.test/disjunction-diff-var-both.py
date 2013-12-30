x = int()
y = int()

if x < 0 or y > 10:
    if x < 5:
        w = (x, y)    # here
    else:
        w = (x, y)    # and here


# (int[-∞..-1], int)
# (int[0..+∞], int[11..+∞])
print w
