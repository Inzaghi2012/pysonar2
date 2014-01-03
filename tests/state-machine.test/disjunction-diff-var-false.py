x = int()
y = int()

if x < 0 or y > 15:
    if x > 5 and y < 10:
        u = (x, y)
    else:
        u = (x, y)   # here


# displays 4 possibilities for (x, y):
# (-inf, 0), (-inf, 10)
# (-inf, 0), [10, +inf) 
# (5, +inf), (15, +inf) 
# [0, 5], (15, +inf)

# TODO: can be simlified to 2 cases:
# (-inf, 0), (-inf, +inf)
# [0, +inf), (15, +inf) 

print u
