(do
	(= x 10)
	(while 
		(>= x (+ 1 0))
		(do
			(= x (- x 1))
			(cond 
				(! (|| (== x 7) (== x 3))) // (&& (<> x 7) (<> x 3))
				(print x)
				(else 
					(print (+ x 100))
				)
			)
		)
	)
)
