package jumbo.algo;

import java.util.List;

import jumbo.data.JumboCut;

public record AlgoResult(JumboCut best, List<Integer> missingItems) {

}
