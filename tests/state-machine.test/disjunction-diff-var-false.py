x = int()
y = int()

if x < 0 or y > 15:
    if x > 5 and y < 10:
        u = (x, y)
    else:
        u = (x, y)   # here


# displays 4 possibilities for (x, y):
# (int[-∞..-1], int[-∞..9]) 
# (int[-∞..-1], int[10..+∞]) 
# (int[6..+∞], int[16..+∞]) 
# (int[0..5], int[16..+∞])

# TODO: can be simlified to 2 cases:
# (int[-∞..-1], int)
# (int[0..+∞], int[16..+∞]) 

print u
