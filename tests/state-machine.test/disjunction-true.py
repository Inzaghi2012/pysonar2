x = int()
y = int()

if x < 3 or y > 10:
    if x < 4 or y > 8:
        u = x      # here
    else:
        u = x

print u
