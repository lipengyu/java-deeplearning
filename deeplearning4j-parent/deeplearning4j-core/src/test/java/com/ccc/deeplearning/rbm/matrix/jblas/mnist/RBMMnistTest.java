package com.ccc.deeplearning.rbm.matrix.jblas.mnist;

import org.apache.commons.math3.random.MersenneTwister;
import org.jblas.DoubleMatrix;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccc.deeplearning.da.DenoisingAutoEncoder;
import com.ccc.deeplearning.datasets.DataSet;
import com.ccc.deeplearning.datasets.iterator.impl.MnistDataSetIterator;
import com.ccc.deeplearning.datasets.mnist.draw.DrawMnistGreyScale;
import com.ccc.deeplearning.rbm.RBM;
import com.ccc.deeplearning.util.MatrixUtil;

public class RBMMnistTest {
	private static Logger log = LoggerFactory.getLogger(RBMMnistTest.class);

	@Test
	public void testMnist() throws Exception {
		MnistDataSetIterator fetcher = new MnistDataSetIterator(20,10);
		MersenneTwister rand = new MersenneTwister(123);

		RBM da = new RBM.Builder().numberOfVisible(784).numHidden(500).withRandom(rand)
				.withMomentum(0.1).build();


		DataSet first = fetcher.next();
		do {
			da.trainTillConvergence(0.1,1,first.getFirst());
		}while(da.lossFunction() > 1);
		first = fetcher.next();
		while(da.lossFunction() > 1)
			da.trainTillConvergence(0.1,1,first.getFirst());


		DoubleMatrix reconstruct = da.reconstruct(first.getFirst());

		for(int i = 0; i < first.numExamples(); i++) {
			DoubleMatrix draw1 = first.get(i).getFirst().mul(255);
			DoubleMatrix reconstructed2 = reconstruct.getRow(i);
			DoubleMatrix draw2 = MatrixUtil.binomial(reconstructed2,1,new MersenneTwister(123)).mul(255);

			DrawMnistGreyScale d = new DrawMnistGreyScale(draw1);
			d.title = "REAL";
			d.draw();
			DrawMnistGreyScale d2 = new DrawMnistGreyScale(draw2,100,100);
			d2.title = "TEST";
			d2.draw();
			Thread.sleep(10000);
			d.frame.dispose();
			d2.frame.dispose();

		}



	}


}