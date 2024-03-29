import React from 'react'

import {Badge, Button, SegmentedControl} from 'antd-mobile';

import classNames from 'classnames/bind';
import styles from '../detail/guavaTradeOption.module.scss';
import CaretDownOutlined from '@ant-design/icons/es/icons/CaretDownOutlined';
import {getEndArea, getStartArea} from '../constant';
import {useRecoilState, useRecoilValue} from 'recoil';
import {
    filterAreaState,
    regionState,
    showAreaFilterState,
    tradeTypeState
} from '../datatool/state';

const cx = classNames.bind(styles);

const GuavaVersusTradeOption = () => {
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const [showAreaFilter, setShowAreaFilter] = useRecoilState(showAreaFilterState);
    const region = useRecoilValue(regionState);
    const [tradeType, setTradeType] = useRecoilState(tradeTypeState);

    return (
        <div className={cx('filter_container')}>
            <SegmentedControl
                selectedIndex={tradeType === 'trade' ? 0 : 1}
                onChange={(e) => {
                    let index = e.nativeEvent.selectedSegmentIndex;
                    if (index === 0) {
                        setTradeType('trade');
                    } else {
                        setTradeType('rent');
                    }
                }}
                values={[<Badge>매매</Badge>, <Badge>전/월세</Badge>]}
                tintColor={'#00802E'}
                style={{height: 30, width: 140}}
            />
            <Button className={cx('filter_btn', filterArea[0] === 0 && filterArea[1] === 5 ? '' : 'active')}
                    type={filterArea[0] === 0 && filterArea[1] === 5 ? '' : 'primary'}
                    inline
                    onClick={() => setShowAreaFilter(true)}>
                {
                    filterArea[0] === 0 && filterArea[1] === 5 ? '면적' : `${getStartArea(filterArea[0])}㎡~${getEndArea(filterArea[1])}㎡`
                }
                <CaretDownOutlined/>
            </Button>
        </div>
    );
};

export default GuavaVersusTradeOption;