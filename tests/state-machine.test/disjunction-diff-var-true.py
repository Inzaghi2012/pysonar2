x = int()
y = int()

if x < 0 or y > 15:
    if x < 5 or y > 10:
        u = (x, y)        # here
    else:
        u = (x, y)


# three possible combinations for (x, y)
# (-inf, 0), any 
# [0, 5), (15, +inf)
# [5, +inf), (15, +inf)

# simplify to two cases:
# (-inf, 0), any 
# [0, +inf), (15, +inf)

print u
