//
//  PitcherRankTableViewCell.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 7..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class PitcherRankTableViewCell: UITableViewCell {

    // 투수순위 정보
    @IBOutlet weak var PitcherRank: UILabel!
    @IBOutlet weak var PitcherName: UILabel!
    @IBOutlet weak var PitcherTeam: UILabel!
    @IBOutlet weak var PitcherNum: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
        // Configure the view for the selected state
    }
    

}

