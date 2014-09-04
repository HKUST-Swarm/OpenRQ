/*
 * Copyright 2014 Jose Lopes
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2011-2014, by Vladimir Kostyukov and Contributors.
 * 
 * This file is part of la4j project (http://la4j.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributor(s): -
 */
package net.fec.openrq.util.linearalgebra.vector.dense;


import static net.fec.openrq.util.arithmetic.OctetOps.aTimesB;
import net.fec.openrq.util.linearalgebra.LinearAlgebra;
import net.fec.openrq.util.linearalgebra.factory.Factory;
import net.fec.openrq.util.linearalgebra.io.ByteVectorIterator;
import net.fec.openrq.util.linearalgebra.io.VectorToBurningIterator;
import net.fec.openrq.util.linearalgebra.vector.AbstractByteVector;
import net.fec.openrq.util.linearalgebra.vector.ByteVector;
import net.fec.openrq.util.linearalgebra.vector.operation.VectorOperation;
import net.fec.openrq.util.linearalgebra.vector.operation.VectorVectorOperation;


public abstract class DenseByteVector extends AbstractByteVector {

    public DenseByteVector(int length) {

        super(LinearAlgebra.DENSE_FACTORY, length);
    }

    @Override
    public <T> T pipeTo(VectorOperation<T> operation) {

        return operation.apply(this);
    }

    @Override
    public <T> T pipeTo(VectorVectorOperation<T> operation, ByteVector that) {

        return that.pipeTo(operation.curry(this));
    }

    /**
     * Converts this dense vector to byte array.
     * 
     * @return array representation of this vector
     */
    public abstract byte[] toArray();

    @Override
    public ByteVector multiply(byte value, Factory factory) {

        ensureFactoryIsNotNull(factory);
        ByteVector result = blank(factory);

        for (int i = 0; i < length(); i++) {
            result.set(i, aTimesB(safeGet(i), value));
        }

        return result;
    }

    @Override
    public void multiplyInPlace(byte value) {

        // TODO: multiply by 0 = clear()
        for (int i = 0; i < length; i++) {
            safeSet(i, aTimesB(safeGet(i), value));
        }
    }

    @Override
    public ByteVectorIterator burningIterator() {

        return new VectorToBurningIterator(iterator()) {

            @Override
            public void flush() {

                // fast flush
                for (int i = innerCursor() + 1; i < length; i++) {
                    DenseByteVector.this.safeSet(i, (byte)0);
                }
            }
        };
    }
}