(do
	(= x 10)
	(while 
		(>= x (+ 1 0))
		(do
			(= x (- x 1))
			(cond 
				(<= x 4)
				(print x)
				(else 
					(print (+ x 100))
				)
			)
		)
	)
)