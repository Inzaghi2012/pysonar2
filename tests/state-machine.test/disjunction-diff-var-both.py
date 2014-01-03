x = int()
y = int()

if x < 0 or y > 10:
    if x < 5:
        w = (x, y)    
        print w       # (-inf, 0), int  or [0, 5), (10, +inf)
    else:
        w = (x, y)    # [5, +inf), (10, +inf)
        print w


# (-inf, 0), int
# [0, 5), (10, +inf)
# [5, +inf), (10, +inf)

print w
