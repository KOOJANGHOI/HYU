//
//  BatterRankTableViewCell.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 7..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class BatterRankTableViewCell: UITableViewCell {

    //타자 기록 정보
    @IBOutlet weak var BatterRank: UILabel!
    @IBOutlet weak var BatterName: UILabel!
    @IBOutlet weak var BatterTeam: UILabel!
    @IBOutlet weak var BatterNum: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
