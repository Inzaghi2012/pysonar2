x = int()

if x < 3:
    w = 10 - x
    print w        # [7, +inf)

    if w == 7:
        print w    # 7
    else:
        print w    # (7, +inf)

else:
    w = 10 - x
    print w  # (-inf, 7)

print w
