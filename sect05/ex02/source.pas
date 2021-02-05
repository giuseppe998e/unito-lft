/* commento **/
(do

  (read x)
	
  (cond (|| (< x 10) (&& (> x 20) (!(> x 30))))

    (print 

			(+ x 100)

		)

    (else

      (print x)

    )

  )

)