x = int()
y = int()

if x < 0 or x > 15:
    if x < 5 or x > 10:
        u = x      # [..-1] [16..]
    else:
        u = x

print u
