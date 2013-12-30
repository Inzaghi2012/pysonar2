x = int()
y = int()

if x < 0 or x > 10:
    if x < 5:
        w = x      # [..-1]
    else:
        w = x      # [11..]

print w            # [..-1] [11..]
