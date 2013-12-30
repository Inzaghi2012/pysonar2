x = int()
y = int()

if x < 0 or y > 15:
    if x < 5 or y > 10:
        u = (x, y)        # here
    else:
        u = (x, y)


# three possible combinations for (x, y)
# (int[-∞..-1], int) 
# (int[0..4], int[16..+∞]) 
# (int[5..+∞], int[16..+∞])
print u
